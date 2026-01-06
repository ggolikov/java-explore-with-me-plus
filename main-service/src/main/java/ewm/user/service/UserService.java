package ewm.user.service;

import ewm.user.dto.NewUserRequest;
import ewm.user.dto.UserDto;
import ewm.user.dto.UserParam;

import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest request);

    List<UserDto> getUsers(UserParam param);

    void delete(Long userId);
}
