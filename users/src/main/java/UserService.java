import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RedisTemplate<String,User> redisTemplate;

    public String addUser(UserRequest userRequest){

        User user = User.builder().userName(userRequest.getUserName()).age(userRequest.getAge()).
                mobNo(userRequest.getMobNo()).build();


        //Save it to the DB
        userRepository.save(user);

        //Save it in the Cache
        saveInCache(user);

        return "User Added Successfully";

    }

    public void saveInCache(User user){

        Map map = objectMapper.convertValue(user, Map.class);
        redisTemplate.opsForHash().putAll(user.getUserName(),map);
        redisTemplate.expire(user.getUserName(), Duration.ofHours(12));

    }

    public User findByUserName(String userName){

        //1.Find in the Redis Cache
        Map map = redisTemplate.opsForHash().entries(userName);

        User user = null;
        //If not found in the Redis/Map
        if(map == null){
            //Find the UserObj from UserRepo
            user = userRepository.findByuserName(userName);

            //Save that founded User in the Cache
            saveInCache(user);

            return user;

        }else{
            //We found the UserObj
            user = objectMapper.convertValue(map,User.class);

            return user;
        }
    }

}
