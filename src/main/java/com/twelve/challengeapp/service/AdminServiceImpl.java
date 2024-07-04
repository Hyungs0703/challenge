package com.twelve.challengeapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twelve.challengeapp.dto.CommentRequestDto;
import com.twelve.challengeapp.dto.CommentResponseDto;
import com.twelve.challengeapp.dto.PostRequestDto;
import com.twelve.challengeapp.dto.PostResponseDto;
import com.twelve.challengeapp.dto.UserRequestDto;
import com.twelve.challengeapp.dto.UserResponseDto;
import com.twelve.challengeapp.entity.Comment;
import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.entity.UserRole;
import com.twelve.challengeapp.exception.AlreadyAdminException;
import com.twelve.challengeapp.exception.CommentNotFoundException;
import com.twelve.challengeapp.exception.PostNotFoundException;
import com.twelve.challengeapp.exception.UserNotFoundException;
import com.twelve.challengeapp.repository.CommentRepository;
import com.twelve.challengeapp.repository.PostRepository;
import com.twelve.challengeapp.repository.UserRepository;

@Service
public class AdminServiceImpl implements AdminService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;

	private final CommentRepository commentRepository;

	public AdminServiceImpl(UserRepository userRepository, PostRepository postRepository,
		CommentRepository commentRepository) {
		this.userRepository = userRepository;
		this.postRepository = postRepository;
		this.commentRepository = commentRepository;
	}

	@Override
	public List<UserResponseDto> getAllUsers() {
		return userRepository.findAll()
			.stream()
			.filter(user -> user.getRole() != UserRole.ADMIN)
			.map(UserResponseDto::new)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public UserResponseDto updateUserRole(Long userId, UserRequestDto.Role role) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));

		user.withdrawal(UserRole.from(role.getRole()));

		return new UserResponseDto(user);
	}

	@Override
	@Transactional
	public UserResponseDto promoteToAdmin(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));

		if (user.getRole().equals(UserRole.ADMIN)) {
			throw new AlreadyAdminException("User is already an admin");
		}

		user.withdrawal(UserRole.ADMIN);

		return new UserResponseDto(user);
	}

	@Override
	@Transactional
	public void deleteUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));

		// post, comment 상태는 유지
		user.withdrawal(UserRole.DELETED);
	}

	@Override
	public List<PostResponseDto> getAllPosts() {
		return postRepository.findAll().stream().map(PostResponseDto::new).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));
		post.update(postRequestDto.getTitle(), postRequestDto.getContent());
		return new PostResponseDto(post);
	}

	@Override
	@Transactional
	public void deletePost(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));

		Long userId = post.getUser().getId();
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

		user.removePost(post);
		postRepository.delete(post);
	}

	@Override
	public List<CommentResponseDto> getAllComments() {
		return commentRepository.findAll().stream().map(CommentResponseDto::new).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public CommentResponseDto updateComment(Long commentId, CommentRequestDto requestDto) {

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentNotFoundException("Comment not found"));

		comment.update(requestDto.getContent());
		return new CommentResponseDto(comment);
	}

	@Override
	@Transactional
	public void deleteComment(Long commentId) {

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentNotFoundException("Comment not found"));

		User user = comment.getUser();
		Post post = comment.getPost();

		user.removeComment(comment);
		post.removeComment(comment);
	}
}
