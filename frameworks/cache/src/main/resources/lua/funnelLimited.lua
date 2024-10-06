---
--- 漏斗限流算法
--- Created by cx125800.
--- DateTime: 2024/3/7 14:41
---

-- 漏斗key
funnelKey = 'funnel'
-- 容量
capacityField = 'capacity'
-- 流水速率
leakingRateField = 'leakingRate'
-- 剩余空间
leftQuotaField = 'leftQuota'
-- 上次流水时间
leakingTsField = 'leakingTs'

-- 时间窗口大小，用于设置过期时间，单位秒
period = 60
-- 漏斗容量
capacity = 100
-- begin

leakingRate = capacity / period

now = tonumber(ARG[1])


-- 代码中初始化

if redis.call('exists', funnelKey) == 0 then
    redis.call('HSET', funnelKey, capacityField, capacity)
    redis.call('HSET', funnelKey, leakingRateField, leakingRate)
    redis.call('HSET', funnelKey, leftQuotaField, capacity)
    redis.call('HSET', funnelKey, leakingTsField,  now)
end


function makeRoom()

    local leakingTs = redis.call('HGET', funnelKey, leakingTsField)

    local deltaTs = now - leakingTs

    local deltaQuota = math.ceil(deltaTs * leakingRate)

    local capacity = redis.call('HGET', funnelKey, capacityField)

    -- 漏斗溢出，设置为最大容量
    if deltaQuota < 0 then
        redis.call('HSET', funnelKey, leftQuotaField, capacity)
        redis.call('HSET', funnelKey, leakingTsField,  now)
        return
    end

    -- 不能够腾出容量
    if deltaQuota < 1 then
        return
    end

    local currentCapacity = redis.call('HGET', funnelKey, leftQuotaField)
    redis.call('HSET', funnelKey, leakingTsField,  now)
    -- 不能超过最大容量
    if currentCapacity + deltaQuota > capacity then
        redis.call('HSET', funnelKey, leftQuotaField, capacity)
        return
    end
    redis.call('HINCRBY', funnelKey, leftQuotaField, deltaQuota)
end

-- 尝试腾出空间
makeRoom()

local leftQuota = redis.call('HGET', funnelKey, leftQuotaField)
if leftQuota > 1 then
    redis.call('HINCRBY', funnelKey, leftQuotaField, -1)
    redis.call('EXPIRE', funnelKey, period)
    return false
end

return false