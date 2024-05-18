import React, { useEffect, useState } from 'react';
import { postToMe } from '@/apis/tome/post-tome';
import Button from '@/atoms/Button';
import styled from 'styled-components';
import { Navigate, useNavigate } from 'react-router-dom';
import { useUsername } from '@/hooks/useUsername';
import { useRecoilValue } from 'recoil';
import userSessionAtom from '@/recoil/atoms/userSession';
import { INewFromMe, postFromMe } from '@/apis/fromme/post-fromme';
import { INewToMe } from '@/apis/tome/tome';

/**
 * 나의 피드 -> frome me
 * 친구의 피드 -> to me
 */

const Write = () => {
  const navigate = useNavigate();
  const userSession = useRecoilValue(userSessionAtom);

  const username = useUsername();
  const [isMyFeed, setIsMyFeed] = useState<boolean>(username === userSession?.username);
  const [nickname, setNickname] = useState<string>('');
  const [content, setContent] = useState<string>('');
  const [options, setOptions] = useState<string[]>([]);

  const handleClick = async () => {
    if (isMyFeed) {
      await postFromMe(userSession?.username as string, { content, options });
    } else {
      await postToMe(username, { nickname, content, options });
    }
    alert('성공적으로 등록되었어요');
    navigate(`/${username}`);
  };

  return (
    <StyledWrite>
      <div>
        {!isMyFeed ? (
          <>
            From. <Input type="text" onChange={(e) => setNickname(e.currentTarget.value)} />
          </>
        ) : (
          <>From. Me!</>
        )}
      </div>
      <div>
        <TextArea
          placeholder="질문을 남겨보세요."
          onChange={(e) => setContent(e.currentTarget.value)}
        />
      </div>
      {isMyFeed ? (
        <WriteInfo>모든 방문자가 투표할 수 있습니다.</WriteInfo>
      ) : (
        <WriteInfo>무물 주인만 투표할 수 있습니다.</WriteInfo>
      )}
      <Button content={'게시하기'} onClick={handleClick} />
    </StyledWrite>
  );
};

const StyledWrite = styled.div`
  padding: 10px 20px 20px 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

const WriteInfo = styled.span`
  color: ${({ theme }) => theme.PRIMARY};
  font-size: 12px;
  font-weight: bold;
`;

const TextArea = styled.textarea`
  border: none;
  width: 100%;
  min-height: 100px;
  border-bottom: 1px solid ${({ theme }) => theme.BORDER_LIGHT};
`;

const Input = styled.input`
  border: none;
  border-radius: 0;
  border-bottom: 1px solid ${({ theme }) => theme.BORDER_LIGHT};
`;

export default Write;
