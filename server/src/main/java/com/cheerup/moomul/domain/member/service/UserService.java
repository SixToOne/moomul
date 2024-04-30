package com.cheerup.moomul.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cheerup.moomul.domain.member.entity.IdCheckResponseDto;
import com.cheerup.moomul.domain.member.entity.LoginRequestDto;
import com.cheerup.moomul.domain.member.entity.LoginResponseDto;
import com.cheerup.moomul.domain.member.entity.ProfileDto;
import com.cheerup.moomul.domain.member.entity.ProfileModifyRequestDto;
import com.cheerup.moomul.domain.member.entity.ProfileResponseDto;
import com.cheerup.moomul.domain.member.entity.SignUpDto;
import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.entity.UserDetailDto;
import com.cheerup.moomul.domain.member.jwt.JwtProvider;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.global.response.BaseException;
import com.cheerup.moomul.global.response.ErrorCode;
import com.cheerup.moomul.global.util.S3Uploader;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final S3Uploader s3Uploader;

	public Void signUp(SignUpDto signUpDto) {
		userRepository.save(
			User.builder()
			.username(signUpDto.username())
			.password(signUpDto.password())
			.nickname(signUpDto.nickname())
			.build());
		return null;
	}

	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
		User user=userRepository.findByUsername(loginRequestDto.username())
			.orElseThrow(()->new BaseException(ErrorCode.NO_USER_ERROR));

		if(!user.getPassword().equals(loginRequestDto.password()))
			throw new BaseException(ErrorCode.WRONG_PASSWORD);

		String accessToken=jwtProvider.createToken(user.getId(),1000L * 60 * 60 * 24 * 7);
		String refreshToken=jwtProvider.createToken(user.getId(),1000L * 60 * 60 * 24 * 7);


		return new LoginResponseDto(
			user.getId(),
			accessToken,
			refreshToken);

	}

	public ProfileResponseDto profile(Long userId, UserDetailDto loginUserId) {
		boolean isMine=false;
		if(loginUserId!=null&&loginUserId.Id().equals(userId)){
			isMine=true;
		}
		User curUser=userRepository.findById(userId)
			.orElseThrow(()->new BaseException(ErrorCode.NO_USER_ERROR));

		ProfileDto profileDto= userRepository.findProfileById(userId);

		return new ProfileResponseDto(profileDto.nickname(),
			profileDto.content(),
			profileDto.image(),
			isMine,
			profileDto.toMe(),
			profileDto.fromMe(),
			0L);
	}

	public IdCheckResponseDto idCheck(String username) {
		return new IdCheckResponseDto(userRepository.findByUsername(username).isEmpty());
	}

	public ProfileResponseDto modifyProfile(UserDetailDto user, ProfileModifyRequestDto profileModifyRequestDto) {
		User curUser=userRepository.findById(user.Id())
			.orElseThrow(()->new BaseException(ErrorCode.NO_USER_ERROR));
		curUser.updateUser(profileModifyRequestDto.nickname(), profileModifyRequestDto.content());

		return profile(user.Id(),new UserDetailDto(curUser.getId()));
	}

	@Transactional
	public ProfileResponseDto modifyProfileImage(UserDetailDto user, MultipartFile image) {
		User curUser=userRepository.findById(user.Id())
			.orElseThrow(()->new BaseException(ErrorCode.NO_USER_ERROR));

		String imageUrl=s3Uploader.saveFile(image);
		String beforeUrl=curUser.getImage();

		if(beforeUrl!=null){
			s3Uploader.deleteFile(beforeUrl);
		}
		curUser.updateUserImage(imageUrl);

		return profile(user.Id(),new UserDetailDto(curUser.getId()));
	}

}
