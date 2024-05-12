package com.cheerup.moomul.domain.member.entity;

import java.util.ArrayList;
import java.util.List;

import com.cheerup.moomul.domain.BaseEntity;
import com.cheerup.moomul.domain.post.entity.Comment;
import com.cheerup.moomul.domain.post.entity.PostLike;
import com.cheerup.moomul.domain.post.entity.Vote;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	private String username;

	private String password;

	private String nickname;

	private String content;

	private String image;

	@OneToMany(mappedBy = "user")
	private List<PostLike> postLikeList = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Comment> commentList = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Vote> voteList = new ArrayList<>();

	public void updateUser(String nickname,String content){
		this.nickname=nickname;
		this.content=content;
	}
	public void updateUserImage(String image){
		this.image=image;
	}
}
