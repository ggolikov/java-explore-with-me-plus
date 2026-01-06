package ewm.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserParam {
    List<Long> ids;
    Integer from;
    Integer size;

    public UserParam(List<Long> ids, Integer from, Integer size) {
        this.ids = ids;
        this.from = from;
        this.size = size;
    }
}
