import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import MoomulCard from '@/organisms/MoomulCard';
import { getNotRepliedToMe, getRepliedToMe } from '@/apis/tome/get-tome';
import { IToMe } from '@/apis/tome/tome';
import Tabs, { ITab } from '@/atoms/Tabs';
import { patchVote } from '@/apis/tome/patch-vote';
import { useRecoilValue } from 'recoil';
import userSessionAtom from '@/recoil/atoms/userSession';
import { IFromMe } from '@/apis/fromme/fromme';
import { getFromMe } from '@/apis/fromme/get-fromme';
import Login from '@/pages/Login';
import { patchFromMe } from '@/apis/fromme/vote-fromme';
import styled from 'styled-components';
import { replyTome } from '@/apis/tome/reply-tome';

type IFeedType = 'toMe' | 'fromMe' | 'notAnswer';

const Feed = () => {
  const { pathname } = useLocation();
  const userSession = useRecoilValue(userSessionAtom);
  const [username, setUsername] = useState<string>('');

  useEffect(() => {
    setUsername(pathname.replace('/', '') || userSession?.username || '');
  }, [userSession, pathname]);

  const [feedType, setFeedType] = useState<IFeedType>('toMe');
  const [toMeData, setToMeData] = useState<IToMe[]>();
  const [fromMeData, setFromMeData] = useState<IFromMe[]>();
  const [notAnswerData, setNotAnswerData] = useState<IFromMe[]>();

  const tabs: ITab[] = [
    {
      name: '투미',
      selected: feedType === 'toMe',
      handleClick: () => setFeedType('toMe'),
    },
    {
      name: '프롬미',
      selected: feedType === 'fromMe',
      handleClick: () => setFeedType('fromMe'),
    },
    {
      name: '답변안함',
      selected: feedType === 'notAnswer',
      handleClick: () => setFeedType('notAnswer'),
    },
  ];

  const fetchNotAnswerData = async () => {
    if (!username) return;
    const data = await getNotRepliedToMe(username, 0, 10);

    setNotAnswerData(() => [...data]);
  };

  const fetchToMeData = async () => {
    if (!username) return;
    const data = await getRepliedToMe(username, 0, 10);
    setToMeData(data);
  };

  const fetchFromMeData = async () => {
    if (!username) return;
    const data = await getFromMe(username, 0, 10);
    console.log(data);
    setFromMeData(data);
  };

  const voteToMe = async (tomeId: number, optionId: number) => {
    const data = await patchVote(username, tomeId, optionId);
    if (!data) {
      alert('피드 주인만 투표할 수 있어요:)');
    }
    fetchToMeData();
  };

  const voteFromMe = async (tomeId: number, optionId: number) => {
    const data = await patchFromMe(username, tomeId, optionId);
    if (!data) {
      alert('로그인해야 투표할 수 있어요:)');
    }
    fetchFromMeData();
  };

  const postReplyTome = async (username: string, tomeId: number, reply: string) => {
    const data = await replyTome(username, tomeId, reply);
  };

  useEffect(() => {
    if (feedType === 'toMe') {
      fetchToMeData();
    } else if (feedType === 'fromMe') {
      fetchFromMeData();
    } else {
      fetchNotAnswerData();
    }
  }, [feedType, username]);

  return (
    <>
      <Tabs tabs={tabs} />
      {feedType === 'toMe' &&
        (toMeData && toMeData.length > 0 ? (
          toMeData.map((tomeItem) => {
            return (
              <MoomulCard
                key={tomeItem.id}
                data={tomeItem}
                voteToMe={voteToMe}
                reply={postReplyTome}
                refetch={fetchToMeData}
              />
            );
          })
        ) : (
          <NoContent>게시글이 없어요:)</NoContent>
        ))}
      {feedType === 'fromMe' &&
        (fromMeData && fromMeData.length > 0 ? (
          fromMeData.map((fromMeItem) => {
            return (
              <MoomulCard
                key={fromMeItem.id}
                data={fromMeItem}
                voteToMe={voteFromMe}
                reply={postReplyTome}
                refetch={fetchFromMeData}
              />
            );
          })
        ) : (
          <NoContent>게시글이 없어요:)</NoContent>
        ))}
      {feedType === 'notAnswer' &&
        (notAnswerData && notAnswerData.length > 0 ? (
          notAnswerData.map((fromMeItem) => {
            return (
              <MoomulCard
                key={fromMeItem.id}
                data={fromMeItem}
                voteToMe={voteFromMe}
                reply={postReplyTome}
                refetch={() => {
                  alert('답변이 등록되었습니다.');
                  setFeedType('toMe');
                }}
              />
            );
          })
        ) : (
          <NoContent>게시글이 없어요:)</NoContent>
        ))}
    </>
  );
};

const NoContent = styled.div`
  width: 100%;
  text-align: center;
  padding: 50px;
  color: ${({ theme }) => theme.LIGHT_BLACK};
`;

export default Feed;
