package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    private Long id;
    private String username;

    //jpa는 빈생성자 무조건 있어야함 아무데서나 접근 막기위해 protected
    //entity는 기본적인 생성자가 있아야함
    //private로 설정하면 proxy에서 막혀버리는 경우가 생김
    protected Member() {
    }

    public Member(String username){
        this.username = username;
    }
}
