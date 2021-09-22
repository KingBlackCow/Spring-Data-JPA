package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})//team은 출력하면 안됨 n+1문제//연관관계에서는 toString 쓰지말자
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username"
)
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "memeber_id")
    private Long id;
    private String username;
    private int age;


    @ManyToOne(fetch = FetchType.LAZY)//지연로딩: 멤버만 조회할 때 딱 멤버만 조회하자 실제 값을 이용할 떄만 가져오자
    @JoinColumn(name = "team_id")
    private Team team;

    //jpa는 빈생성자 무조건 있어야함 아무데서나 접근 막기위해 protected
    //entity는 기본적인 생성자가 있아야함
    //private로 설정하면 proxy에서 막혀버리는 경우가 생김
//    protected Member() {
//    }

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team!=null) {
            changeTeam(team);
        }
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);//반대쪽에 멤버도 반드시 바꾸기!!
    }
}
