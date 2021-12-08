package com.example.services.accessor;

import com.example.models.User;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.example.models.enums.Permission.ADMIN;

public class UserAccessorImplTest {

    @Test
    public void saveUserTest() {

        Map<String, String> answers = new HashMap<>();
        answers.put("1", "Answer One");
        answers.put("2", "Answer Two");
        answers.put("3", "Answer Three");

        UserAccessor accessor = new UserAccessorImpl();
        User user = User.builder()
                .username("TEST_USER")
                .permission(ADMIN)
                .password("PASSCODE")
                .answers(answers)
                .build();

        accessor.save(user);
    }

    @Test
    public void getUserTest() {
        UserAccessor accessor = new UserAccessorImpl();
        accessor.get(User.builder()
                .username("TEST_USER")
                .password("PASSCODE")
                .build());
    }
}
