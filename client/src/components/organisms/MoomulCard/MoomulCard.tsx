import React from 'react';
import Progress from '@/atoms/Progress';
import { styled } from 'styled-components';
import { IToMe } from '@/apis/tome/tome';
import { getPercentage } from '@/utils/get-percentage';
import { getFormattedYearMonthDayTime } from '@/utils/time';
import Icon from '@/components/atoms/Icon';

interface MoomulCardProps {
  data: IToMe;
  voteToMe: (tomeId: number, optionId: number) => Promise<void>;
}

const MoomulCard = ({ data, voteToMe }: MoomulCardProps) => {
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
