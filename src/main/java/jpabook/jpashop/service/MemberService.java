package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 읽기전용으로 성능 올릴수 있음
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional // 개별설정이 우위라 readOnly = false 적용 // 쓰기는 중요하니 기본 tx
    //회원 가입
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 검증 , 이름 중복이 안된다고 가정
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        //EXCEPTION <-문제있을시
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(findMembers.size()>0) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
