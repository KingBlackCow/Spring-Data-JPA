package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing//Auditing 엔티티의 createDate,updateDate알려주는거 쓸려고(BaseEntity참고)
@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.datajpa.repository") 스프링쓸때만 사용
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);


	}
	@Bean//수정자 , 생성자 넣을때 추가(BaseEntity참고)
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.of(UUID.randomUUID().toString());
	}

}
