package bookDB.demo.Service;

import bookDB.demo.Domain.Member;
import bookDB.demo.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) { this.memberRepository = memberRepository; }

    public Member addMember(Member member) {
        if (member.getId() == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return memberRepository.addMember(member);
    }

    public void deleteMember(Long id) {
        try {
            memberRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw e;
        }
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    public Member login(Long id, String password) {
        if (id == null || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("ID and password cannot be null or empty");
        }
        Member member = memberRepository.findById(id);
        if (member == null) {
            throw new IllegalArgumentException("No member found with the given ID");
        }
        if (!member.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password");
        }
        return member;
    }
}
