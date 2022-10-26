package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

//    @PersistenceContext //애너테이션이 em에 entitymanager를 주입해줌
    private final EntityManager em; // <- 스프링부트 jpa가 지원

    public void save(Member member){
        em.persist(member);//insert
    }

    public Member findOne(Long id){
        return em.find(Member.class,id);//단건조회
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m",Member.class) //jpql사용
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name",Member.class) //jpql사용
                .setParameter("name",name)
                .getResultList();
    }
}
