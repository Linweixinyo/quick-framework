---
--- 计数器滑动窗口算法
--- Created by cx125800.
--- DateTime: 2024/3/7 14:29
---

-- 计数器 field
local counterKey = 'slidingWindow'
-- 时间窗口大小 60秒
local period = 60
-- 请求限制次数 100次
local limited = 100

local now = tonumber(ARGV[1])

redis.call('ZREMRANGEBYSCORE', counterKey, 0, now - period)

local currentCount = redis.call('ZCARD', counterKey)

if currentCount > limited then
    return false
end

redis.call('ZADD', counterKey, now, now)

redis.call('EXPIRE', counterKey, period / 1000)
return true
