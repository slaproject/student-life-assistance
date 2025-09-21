package com.studentapp.backend.repository;

import com.studentapp.common.model.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, UUID> {

    @Query("SELECT ta FROM TaskAttachment ta WHERE ta.task.id = :taskId ORDER BY ta.uploadedAt DESC")
    List<TaskAttachment> findByTaskIdOrderByUploadedAtDesc(@Param("taskId") UUID taskId);

    @Query("SELECT ta FROM TaskAttachment ta WHERE ta.task.userId = :userId ORDER BY ta.uploadedAt DESC")
    List<TaskAttachment> findByUserIdOrderByUploadedAtDesc(@Param("userId") UUID userId);

    @Query("SELECT COUNT(ta) FROM TaskAttachment ta WHERE ta.task.id = :taskId")
    Long countByTaskId(@Param("taskId") UUID taskId);

    @Query("SELECT SUM(ta.fileSize) FROM TaskAttachment ta WHERE ta.task.userId = :userId")
    Long getTotalFileSizeByUserId(@Param("userId") UUID userId);

    @Query("SELECT CASE WHEN COUNT(ta) > 0 THEN true ELSE false END FROM TaskAttachment ta WHERE ta.task.id = :taskId AND ta.fileName = :fileName")
    boolean existsByTaskIdAndFileName(@Param("taskId") UUID taskId, @Param("fileName") String fileName);
}