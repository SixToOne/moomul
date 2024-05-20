import React, { useCallback, useEffect, useState } from 'react';
import Progress from '@/atoms/Progress';
import { styled } from 'styled-components';
import { IToMe } from '@/apis/tome/tome';
import { getPercentage } from '@/utils/get-percentage';
import { getFormattedYearMonthDayTime } from '@/utils/time';
import Icon from '@/atoms/Icon';
import Input from '@/components/atoms/Input';
import Button from '@/components/atoms/Button';
import { useUsername } from '@/hooks/useUsername';

interface MoomulCardProps {
  data: IToMe;
  voteToMe: (tomeId: number, optionId: number) => Promise<void>;
  reply: (username: string, tomeId: number, reply: string) => Promise<void>;
  refetch: () => void;
}

const MoomulCard = ({ data, voteToMe, reply, refetch }: MoomulCardProps) => {
  const [replyMode, setReplyMode] = useState<boolean>(false);
  const [replyContent, setReplyContent] = useState<string>('');
  const [sendMode, setSendMode] = useState<boolean>(false);
  const username = useUsername();

  useEffect(() => {
    if (!sendMode) return;
    reply(username, data.id, replyContent);
    refetch();
  }, [sendMode, username, data.id, replyContent]);

  const handleClick = () => {
    setSendMode(true);
  };

  const ReplyMode = useCallback(() => {
    if (replyMode) {
      return (
        <div>
          <Input
            label={'무물 답변 달기'}
            name={'reply'}
            handleInput={(e) => {
              const value = e.currentTarget.value;
              setReplyContent(() => value);
            }}
          />
          <ReplyButtons>
            <Button content={'등록'} onClick={handleClick} />
            <Button content={'취소'} onClick={() => setReplyMode(false)} />
          </ReplyButtons>
        </div>
      );
    }
    return <ReplyButton onClick={() => setReplyMode(true)}>무물 답변 달기</ReplyButton>;
  }, [replyMode]);

  return (
    <StyledMoomulCard>
      <div className="moomul-card__info">
        <span className="profile__nickname">{data.nickname}</span>
        <span className="moomul-card__created-at">
          {getFormattedYearMonthDayTime(new Date(data.createdAt))}
        </span>
      </div>
      <div className="moomul-card__content">
        <span>{data.content}</span>
      </div>
      {data.options.length > 0 && (
        <div className="moomul-card__vote-form">
          {data.options.map((option) => {
            return (
              <button key={option.id} onClick={() => voteToMe(data.id, option.id)}>
                <Progress
                  max={100}
                  value={getPercentage(data.voteCnt, option.voteCnt)}
                  content={option.content}
                  color={data.voted ? 'primary' : 'gray'}
                />
              </button>
            );
          })}
        </div>
      )}
      <Reply>{data.reply ? data.reply : <ReplyMode />}</Reply>
      {data.options.length > 0 && (
        <div className="moomul-card__vote-count">{data.voteCnt}명 투표</div>
      )}
      <div className="moomul-card__like-and-reply">
        <div className="moomul-card__like">
          <Icon name={'Heart'} width="16px" height="16px" />
          <span>좋아요 {data.likeCnt}</span>
        </div>
        <div>댓글 {data.commentCnt}</div>
      </div>
    </StyledMoomulCard>
  );
};

const Reply = styled.div`
  border-left: 3px solid ${({ theme }) => theme.PRIMARY};
  padding: 10px;
  margin-bottom: 5px;
`;

const ReplyButton = styled.button`
  font-size: 16px;
  color: ${({ theme }) => theme.LIGHT_BLACK};
  border: 1px solid ${({ theme }) => theme.BORDER_LIGHT};
  padding: 4px;
  border-radius: 3px;
`;

const ReplyButtons = styled.div`
  display: flex;
  height: 22px;
  gap: 6px;
  margin-top: 10px;
`;

const StyledMoomulCard = styled.div`
  padding: 24px;
  border-bottom: 1px solid ${({ theme }) => theme.BORDER_LIGHT};

  .moomul-card__info {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .profile__nickname {
    }

    .moomul-card__created-at {
      font-size: 14px;
      color: ${({ theme }) => theme.LIGHT_BLACK};
    }
  }

  .moomul-card__content {
    padding: 16px 0;
  }

  .moomul-card__vote-form {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-bottom: 16px;
  }

  .moomul-card__vote-count {
    padding: 10px 0;
    font-size: 12px;
    color: ${({ theme }) => theme.LIGHT_BLACK};
  }

  .moomul-card__like-and-reply {
    width: 100%;
    padding: 10px 0;
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    color: ${({ theme }) => theme.DARK_BLACK};

    .moomul-card__like {
      display: flex;
      align-items: center;
      gap: 3px;
    }
  }
`;

export default MoomulCard;
