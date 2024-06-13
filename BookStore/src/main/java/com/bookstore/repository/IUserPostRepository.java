    package com.bookstore.repository;

    import com.bookstore.entity.Role;
    import com.bookstore.entity.User_Post;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.stereotype.Repository;

    import java.util.List;
    import java.util.Optional;
    public interface IUserPostRepository extends JpaRepository<User_Post, Long> {
        List<User_Post> findByClassEntityId(Long classId);
        List<User_Post> findBySubjectEntityId(Long subjectId);

    }
