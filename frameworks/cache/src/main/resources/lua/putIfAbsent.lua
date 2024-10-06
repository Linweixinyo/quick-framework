---
--- Created by cx125800.
--- DateTime: 2024/2/11 23:40
---

local key = KEYS[1]
local value = ARGV[1]
local ttl = ARGV[2]

-- redis中该key存在
if redis.call('exists', key) == 1
then
    -- 该key剩余的ttl
    local ttlOfRemain = redis.call('ttl', key)
    if ttlOfRemain ~= -1
    then
        redis.call('SETEX', key, ttlOfRemain, value)
    end
else
    redis.call('SETEX', key, ttl, value)
end

return true




