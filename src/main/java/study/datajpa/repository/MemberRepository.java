package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> { //멤버는 만들객체 Long은 id값

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

    //@Query(name ="Member.findByUsername") //없어도 되는 이유 JpaRepository가 namedQuery를 먼저 찾음
    List<Member> findByUsername(@Param("username")String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username")String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //dto로 조회시 "new 패키지 경로" 를 붙여야한다.
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //단건 Optional

    @Query(value ="select m from Member m left join m.team t",
            countQuery ="select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
    //Slice<Member> findByAge(int age, Pageable pageable);//리스트로 받아도됨

    //@Modifying//executeUpdate() 역할 (update 문에서는 필수)
    @Modifying(clearAutomatically =true)
    @Query("update Member m set m.age = m.age +1 where m.age>= :age")
    int bulkAgePlus(@Param("age") int age);
}
