package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    @Value("#{target.username + ' ' + target.age}")//member를 다가져오고 새로 이름을 넣음
    String getUsername();
}
