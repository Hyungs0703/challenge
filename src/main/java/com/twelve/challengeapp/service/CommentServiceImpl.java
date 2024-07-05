package com.twelve.challengeapp.service;

import com.twelve.challengeapp.dto.CommentResponseDto;
import com.twelve.challengeapp.entity.Comment;
import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.exception.DuplicateException;
import com.twelve.challengeapp.exception.NotFoundException;
import com.twelve.challengeapp.exception.NotAuthorizedException;
import com.twelve.challengeapp.repository.CommentRepository;
import com.twelve.challengeapp.repository.PostRepository;
import com.twelve.challengeapp.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public CommentResponseDto createComment(Long postId, String content, Long userId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));

		Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found."));

		if (post.getUser().equals(user)) {
			throw new DuplicateException("Comments on your own post are not allowed.");
		}

		Comment comment = Comment.builder().content(content).user(user).build();

		user.addComment(comment);
		post.addComment(comment);

		commentRepository.flush();

		return new CommentResponseDto(comment);
	}

	@Override
	@Transactional
	public CommentResponseDto updateComment(Long commentId, String content, User user) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new NotFoundException("Comment not found"));
		if (!comment.getUser().equals(user)) {
			throw new NotAuthorizedException("You are not authorized to update this comment");
		}

		comment.update(content);

		return new CommentResponseDto(comment);
	}

	@Override
	@Transactional
	public void deleteComment(Long commentId, Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new NotFoundException("Comment not found"));

		if (!comment.getUser().equals(user)) {
			throw new NotAuthorizedException("You are not authorized to delete this comment");
		}

		Post post = comment.getPost();
		user.removeComment(comment);
		post.removeComment(comment);
	}

	@Override
	public List<CommentResponseDto> getCommentsByPostId(Long postId) {
		return commentRepository.findByPostId(postId)
			.stream()
			.map(CommentResponseDto::new)
			.collect(Collectors.toList());
	}
}
