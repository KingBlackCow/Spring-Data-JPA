package study.datajpa.repository;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional//테스트파일에서 Transactional입력 필수 테스트 파일이라 바로 삭제시켜줌
@Rollback(false)
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());
        assertThat((findMember.getId())).isEqualTo(member.getId());
        assertThat((findMember.getUsername())).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }
}