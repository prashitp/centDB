package com.example.services.accessor;

import com.example.models.User;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import static com.example.util.Constants.COLON_DELIMITER;
import static com.example.util.Constants.PIPE_DELIMITER;

public class UserAccessorImpl implements UserAccessor {

    private FileWriter fileWriter;
    String userFilePath = "storage/authentication/users.txt";
    String questionFilePath = "storage/authentication/questions.txt";

    @SneakyThrows
    public UserAccessorImpl() {
        this.fileWriter = new FileWriter(userFilePath, true);
    }

    @Override
    @SneakyThrows
    public Boolean save(User user) {
        String userString = user.getUsername().concat(PIPE_DELIMITER)
                .concat(user.getPassword()).concat(PIPE_DELIMITER)
                .concat(user.getPermission().name()).concat(PIPE_DELIMITER);

        String answers = "";
        for (Map.Entry<String, String> entry : user.getAnswers().entrySet()) {
            answers = answers.concat(entry.getKey()).concat(COLON_DELIMITER)
                    .concat(entry.getValue()).concat(PIPE_DELIMITER);
        }

        fileWriter.write(userString.concat(answers).concat("\n"));
        fileWriter.flush();
        return null;
    }

    @Override
    public Optional<User> get(User user) {
        Optional<List<String>> optional = getLine(user.getUsername());
        if (optional.isPresent()) {
            List<String> details = optional.get();
            if (user.getUsername().equals(details.get(0)) && user.getPassword().equals(details.get(1))) {
                return Optional.of(User.builder()
                        .username(details.get(0))
                        .password(details.get(1))
                        .build());
            }
        }
        return Optional.empty();
    }

    @Override
    public Boolean isPresent(String username) {
        return getLine(username).isPresent();
    }

    @SneakyThrows
    private Optional<List<String>> getLine(String username) {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(userFilePath));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] strings = line.split("\\|");
            if (username.equals(strings[0])) {
                return Optional.of(Arrays.asList(strings));
            }
        }
        return Optional.empty();
    }

    @SneakyThrows
    public Map<String, String> getQuestions() {
        Map<String, String> map = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(questionFilePath));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] strings = line.split("\\|");
            map.put(strings[0], strings[1]);
        }
        return map;
    }
}
