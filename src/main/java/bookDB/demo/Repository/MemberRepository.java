package bookDB.demo.Repository;

import bookDB.demo.Domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member addMember(Member member);

    void deleteById(Long id);

    Member findById(Long id);

    List<Member> findAll();
}
