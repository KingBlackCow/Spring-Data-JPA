package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long>, MemberRepositoryCustom,JpaSpecificationExecutor<Member> { //멤버는 만들객체 Long은 id값

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

    //파라미터바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //단건 Optional

    @Query(value ="select m from Member m left join m.team t",
            countQuery ="select count(m) from Member m")//countQuery를 안쓰면 전체데이터를 가져올때 쿼리가 두 번 날아가기떄문에
    Page<Member> findByAge(int age, Pageable pageable);
    //Slice<Member> findByAge(int age, Pageable pageable);//리스트로 받아도됨

    //@Modifying//executeUpdate() 역할 (update 문에서는 필수)
    @Modifying(clearAutomatically =true)
    @Query("update Member m set m.age = m.age +1 where m.age>= :age")
    int bulkAgePlus(@Param("age") int age);

    //n+1 해결법
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();


    //바로 위와 동일
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //@EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all") //네임드커리같이 네임드엔티티그래프(Member.class에 정의)를 이용하는 방법
    List<Member> findEntityGraphByUsername(@Param("username") String username);


    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //select for update 업데이트하는 동안 손대지마!!
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    //동적 프로젝션
    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String name);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName "+
            "from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);

}
