---
--- 计数器固定窗口算法
--- Created by cx125800.
--- DateTime: 2024/3/7 14:20
---

-- 计数器 field
local counterKey = 'limitedCounter'
-- 时间窗口大小 60秒
local period = 60
-- 请求限制次数 100次
local limited = 100

--  对 counter 用 INCR 命令 + 1，如果返回值 > 100 那么就拒绝请求
if redis.call('INCR', counterKey) > limited then
    return false
end

-- 可以请求，设置过期时间后返回 true 存在临界值问题
redis.call("EXPIRE", counterKey, period);
return true