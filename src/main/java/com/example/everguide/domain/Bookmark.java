package com.example.everguide.domain;

import com.example.everguide.domain.common.BaseEntity;

import com.example.everguide.domain.enums.BookmarkType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "welfare_service_id")
    private WelfareService welfareService;

    @JoinColumn(name = "education_id")
    private Education education;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookmarkType type;

    // 북마크 생성 메서드
    public static Bookmark createWelfareBookmark(Member member, WelfareService welfareService) {
        Bookmark bookmark = new Bookmark();
        bookmark.setMember(member);
        bookmark.setWelfareService(welfareService);
        bookmark.setType(BookmarkType.WELFARE);
        return bookmark;
    }
}
