package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberDataJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberDataJpaRepository memberDataJpaRepository;

    @Test
    public void testEntity(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",20,teamA);
        Member member3 = new Member("member3",30,teamB);
        Member member4 = new Member("member4",40,teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush();
        em.clear();

        //확인
        List<Member> members=em.createQuery("select m from Member m",Member.class)
                .getResultList();

        for (Member member:members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() throws Exception {
        Member member = new Member("member1");
        memberDataJpaRepository.save(member);//PrePersist

        Thread.sleep(100);
        member.setUsername("member2");

        em.flush();//PreUpdate
        em.clear();
        Member findMember = memberDataJpaRepository.findById(member.getId()).get();
        System.out.println("create: " +findMember.getCreatedDate());
        System.out.println("update: " +findMember.getLastModifiedDate());
        System.out.println("createdBy: " +findMember.getCreatedBy());
        System.out.println("updatedBy: " +findMember.getLastModifiedBy());
        //두개가 랜덤이라 다르지만 실제로는 세션같은데에서 쓰면 같음
    }
}