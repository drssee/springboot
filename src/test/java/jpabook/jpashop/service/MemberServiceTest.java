package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional // 테스트클래스에서 사용되면 성공해도 롤백
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("Kim");

        //when
        Long saveId = memberService.join(member); //jpa는 commit시 영속성 컨텍스트를 flush하며 쿼리를 날림

        //then
        em.flush();
        Assertions.assertEquals(member,memberRepository.findOne(saveId)); //같은 Member 영속성 컨텍스트 안에서 실행되면 같은 객체를 이용함,참조변수 주소가 같음
    }
    
//    @Test(expected = IllegalStateException.class)
    @Test
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");
        //when
        memberService.join(member1);
        try{
            memberService.join(member2); //예외가 발생해야 한다
        } catch(IllegalStateException e){
            return; //예상되는 illegalexception이 터지면 return으로 성공
        }
        //then
        Assertions.fail("예외가 발생해야 한다."); // fail에 도달하면 실패
    }

}