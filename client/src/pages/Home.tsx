import React, { useState } from 'react';
import Feed from '@/organisms/Feed';
import Profile from '@/organisms/Profile';
import BottomSheet from '@/atoms/BottomSheet';
import { useSheet } from '@/hooks/useSheet';
import Button from '@/atoms/Button';
import { postToMe } from '@/apis/tome/post-tome';

const Home = () => {
  const { open, openSheet, closeSheet } = useSheet();

  return (
    <>
      <Profile />
      <Feed />
      {/* <BottomSheet title="To. 뿡뿡이" open={open} closeSheet={closeSheet}>
        <Button
          content={'등록'}
          onClick={() =>
            postToMe('seongeuniii', {
              nickname: '이바보야진짜',
              content: '오늘 저녁 모머글랭?',
              options: ['딸기', '수박', '사과', '블루베리'],
            })
          }
        />
      </BottomSheet> */}
    </>
  );
};

export default Home;
