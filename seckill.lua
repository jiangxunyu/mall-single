local stock = tonumber(redis.call('get', KEYS[1]))

if stock <= 0 then
    return 0
end

stock = stock - 1
redis.call('set', KEYS[1], stock)

return 1