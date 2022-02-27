package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")//id로받아야하지만
    public String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }

    @GetMapping("/members")//http://localhost:8080/members?page=0&size=3
    public Page<MemberDto> list(@PageableDefault(size = 5, sort="username") Pageable pageable){
        //http://localhost:8080/members?page=0&size=3
        //id= 1, id=2, id=3 조회 (size default = 20)

        //http://localhost:8080/members?page=0&size=3&sort=id,desc
        //id내림차순정렬
        Page<Member> page = memberRepository.findAll(pageable);
        //Page<MemberDto> map = page.map(member -> new MemberDto(member));
        Page<MemberDto> map = page.map(MemberDto::new);//위와 동일 이것을 메서드 레퍼런스라함
        //메서드 레퍼런스
        return map;
    }

    @PostConstruct
    public void init(){
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user"+i,i));
        }
    }
}
