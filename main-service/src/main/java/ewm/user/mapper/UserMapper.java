package ewm.user.mapper;

import ewm.user.dto.NewUserRequest;
import ewm.user.dto.UserDto;
import ewm.user.model.User;

public class UserMapper {

    public static User toEntity(NewUserRequest dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return user;
    }

    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        return dto;
    }
}