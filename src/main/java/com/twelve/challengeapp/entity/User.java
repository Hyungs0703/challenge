package com.twelve.challengeapp.entity;

import com.twelve.challengeapp.entity.like.CommentLike;
import com.twelve.challengeapp.entity.like.PostLike;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String introduce;

	@Column(nullable = false)
	private String email;

	@Column
	private Long postLikeCount;

	@Column
	private Long commentLikeCount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Post> posts = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<UserPasswordRecord> passwordRecordList = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Comment> comments = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<PostLike> postLikeList = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<CommentLike> commentLikeList = new ArrayList<>();

	public void editUserInfo(String nickname, String introduce) {
		this.nickname = nickname;
		this.introduce = introduce;
	}

	public void ChangePassword(String password) {
		this.password = password;
	}

	public void withdrawal(UserRole role) {
		this.role = role;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		User user = (User) obj;
		return Objects.equals(id, user.id);
	}


	// Post 관련
	public void addPost(Post post) {
		posts.add(post);
		post.setUser(this);
	}

	public void removePost(Post post) {
		posts.remove(post);
		post.setUser(null);
	}

	public void addPasswordRecord(UserPasswordRecord record) {
		this.passwordRecordList.add(record);
		record.setUser(this);
	}

	public void removePasswordRecord(UserPasswordRecord record) {
		this.passwordRecordList.remove(record);
		record.setUser(null);
	}

	public void addComment(Comment comment) {
		this.comments.add(comment);
		comment.setUser(this);
	}

	public void removeComment(Comment comment) {
		this.comments.remove(comment);
		comment.setUser(null);
	}
	public void addPostLikeCount(Post post) {
		PostLike postLike = PostLike.builder().user(this).post(post).build();
		postLikeList.add(postLike);
		if(this.postLikeCount == null) {
			this.postLikeCount = 0L;
		}
		this.postLikeCount++;
	}

	public void addCommentLikeCount(Comment comment) {
		CommentLike commentLike = CommentLike.builder().user(this).comment(comment).build();
		commentLikeList.add(commentLike);
		if (this.commentLikeCount == null) {
			this.commentLikeCount = 0L;
		}
		this.commentLikeCount++;
	}
}
