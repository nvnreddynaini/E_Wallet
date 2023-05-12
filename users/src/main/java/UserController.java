import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/add")
    public String createUser(UserRequest userRequest){
        return userService.addUser(userRequest);
    }

    @GetMapping("/find_by_user/{userName}")
    public User findByUserName(@PathVariable String userName){
        return userService.findByUserName(userName);
    }
}
