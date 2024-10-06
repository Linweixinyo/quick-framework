---
--- 令牌桶限流算法
--- Created by cx125800.
--- DateTime: 2024/3/7 15:41
---

local tokenKey = 'tokenBucket'
local capacityField = 'capacity'
local tokensFiled = 'tokens'
local lastAddTsField = 'lastAddTs'
local rateField = 'rate'

-- 时间窗口大小，用于设置过期时间，单位秒
local period = 60
-- 令牌数，容量
local capacity = 100

local rate = capacity / period

local now = ARG[1]

if redis.call('exists', tokenKey) == 0 then
    redis.call('HSET', tokenKey, capacityField, capacity)
    redis.call('HSET', tokenKey, tokensFiled, capacity)
    redis.call('HSET', tokenKey, rateField, rate)
    redis.call('HSET', tokenKey, lastAddTsField,  os.time())
end


local lastAddTs = redis.call('HGET', tokenKey, lastAddTsField)

local deltaTS = now - lastAddTs


local tokens = redis.call('HGET', tokenKey, tokensFiled)

local deltaTokens = math.ceil(deltaTS * rate)

if deltaTokens < 0 then
    redis.call('HSET', tokenKey, tokensFiled, capacity)
    redis.call('HSET', tokenKey, lastAddTsField,  now)
    redis.call('HSET', tokenKey, tokensFiled, capacity - 1)
    return true
end

if tokens == 0 and deltaTokens == 0 then
    return false
end

redis.call('HSET', tokenKey, lastAddTsField,  now)

if tokens + deltaTokens > capacity then
    redis.call('HSET', tokenKey, tokensFiled, capacity - 1)
    redis.call('EXPIRE', tokenKey, period)
    return true
end

redis.call('HINCRBY', tokenKey, tokensFiled, deltaTokens - 1)
redis.call('EXPIRE', tokenKey, period)
return true
