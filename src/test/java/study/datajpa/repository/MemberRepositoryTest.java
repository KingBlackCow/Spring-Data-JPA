package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        //Optional<Member> findMember = memberRepository.findById(savedMember.getId());
        Member findMember = memberRepository.findById(savedMember.getId()).get();


        assertThat((findMember.getId())).isEqualTo(member.getId());
        assertThat((findMember.getUsername())).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //findMember1.setUsername("member!!!!!"); jpa에서 update를 안해도 되는 이유

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test//전체 조회
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();
        for (MemberDto dto : result) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> list = memberRepository.findListByUsername("AAA");
        Member member = memberRepository.findMemberByUsername("AAA");
        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("AAA");
    }

    @Test
    public void paging() {

        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        //3번쨰인자인 Sort.by는 안넣어도됨
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //Entity -> Dto
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

//        for (Member member: content) {
//            System.out.println("member = "+ member);
//        }
//        System.out.println("totalElement = "+ totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

//        PageRequest pageRequest2 = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
//
//        Slice<Member> slice = memberRepository.findByAge(age, pageRequest2); //리스트로 받아도됨
//        List<Member> content2 = slice.getContent();
//
//        assertThat(content2.size()).isEqualTo(3);
//        //assertThat(slice.getTotalElements()).isEqualTo(5);//전체개수를 가져오는게 없음
//        assertThat(slice.getNumber()).isEqualTo(0);
//        //assertThat(slice.getTotalPages()).isEqualTo(2);//전체개수를 가져오는게 없음
//        assertThat(slice.isFirst()).isTrue();
//        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));
        //save는 기본적으로 flush상태

        //bulkAgePlus는 JPQL이므로 JPQL실행전 flush를 해줌줌
        int resultCount = memberRepository.bulkAgePlus(20);
        //em.flush();//변경된 내용을 db에 반영
        //em.clear();//영속성 컨텍스트 안에 데이터를 날려서 다시 db에서 가져오게함.
        //MemberReopository(@Modifying(clearAutomatically =true) <-이걸 넣어주자

        //벌크(update)연산 후에는 꼭 em.flush(), em.clear()를 하자
        List<Member> result = memberRepository.findListByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);
        //위에꺼는 영속성 컨텍스트에서 가져오기에 40살로 표현됨

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //(1)
        //N+1 문제발생 멤버조회시 팀이 조회되지 않고 member.getTeam().getName()을 통해 한번더 조회하기 떄문에
//        List<Member> members = memberRepository.findAll();
//        for (Member member:members) {
//            System.out.println("member = " + member.getUsername());
//            System.out.println("member.teamClass = " + member.getTeam().getClass());
//            System.out.println("member.team = " + member.getTeam().getName());
//        }

        //(2)
//        List<Member> members = memberRepository.findMemberFetchJoin();
//        for (Member member:members) {
//            System.out.println("member = " + member.getUsername());
//            System.out.println("member.teamClass = " + member.getTeam().getClass());
//            System.out.println("member.team = " + member.getTeam().getName());
//        }

        //fetchjoin 뜻 : join한 후 select을 통해 member에 다 넣어줌줌

        //(3) 엔티티 그래프 사용해서 출력하는 예제
//        List<Member> membersEntityGraph = memberRepository.findAll();
//        for (Member member : membersEntityGraph) {
//            System.out.println("member = " + member.getUsername());
//            System.out.println("member.teamClass = " + member.getTeam().getClass());
//            System.out.println("member.team = " + member.getTeam().getName());
//        }

        //(4)
        List<Member> membersEntityGraph = memberRepository.findEntityGraphByUsername("member1");
        for (Member member : membersEntityGraph) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint(){
        //given
        Member member1 = new Member("member1",10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        //(1)
//        Member findMember = memberRepository.findById(member1.getId()).get();
//        findMember.setUsername("member2");
//
//        //DirtyChecking 이름이 변경된걸 감지함
//        em.flush();

        //(2) @QueryHint 예제 이거는 조회속성이라는 걸 알려줘서 update쿼리가 안날아가게함
        //1차캐시에 저장되는 스냅샷을 만들지 않음 update쿼리도 안날아감
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        //DirtyChecking 이름이 변경된걸 감지함
        em.flush();
    }

    @Test
    public void lock(){
        //given
        Member member1 = new Member("member1",10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        List<Member> findMember = memberRepository.findLockByUsername("member1");
        findMember.get(0).setUsername("member2");

        //DirtyChecking 이름이 변경된걸 감지함
        em.flush();
    }

    @Test
    public void callCustom(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));
        List<Member> result = memberRepository.findMemberCustom();
        for (Member m: result) {
            System.out.println(m);
        }
    }

}