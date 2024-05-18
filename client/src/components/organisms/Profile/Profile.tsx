import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import ImageEditor from '@/molecules/ImageEditor';
import Button from '@/components/atoms/Button';
import { getUserProfile } from '@/apis/user/get-user';
import { IUserProfile } from '@/apis/user/user';
import { Link, useLocation } from 'react-router-dom';
import userSessionAtom from '@/recoil/atoms/userSession';
import { useRecoilValue } from 'recoil';
import Login from '@/pages/Login';
import { postFromMe } from '@/apis/fromme/post-fromme';
import { useUsername } from '@/hooks/useUsername';

const Profile = () => {
  const userSession = useRecoilValue(userSessionAtom);
  const username = useUsername();
  const [userProfileData, setUserProfileData] = useState<IUserProfile>();

  const fetchData = async () => {
    if (!username) return;
    const data = await getUserProfile(username);
    setUserProfileData(data);
  };

  useEffect(() => {
    fetchData();
  }, []);

  if (!userProfileData) return <div>...loading</div>;

  return (
    <StyledProfile>
      <div className="profile__section-1">
        <ImageEditor />
        <div className="profile__section-2">
          <div className="profile__section-3">
            <div className="profile__section-4">
              <span>{userProfileData.toMe}</span>
              <span>투미</span>
            </div>
            <div className="profile__section-4">
              <span>{userProfileData.fromMe}</span>
              <span>프롬미</span>
            </div>
            <div className="profile__section-4">
              <span>{userProfileData.today}</span>
              <span>투데이</span>
            </div>
          </div>
          <div>
            <Link to={`/${username}/write`}>
              <Button
                content={'무물 남기기'}
                onClick={() => console.log('button')}
                fontSize="14px"
              />
            </Link>
          </div>
        </div>
      </div>
      <div className="profile__section-5">
        <div className="nickname">{userProfileData.nickname}</div>
        <div className="introduce">{userProfileData.content}</div>
      </div>
    </StyledProfile>
  );
};

const StyledProfile = styled.div`
  padding: 12px 24px;

  .profile__section-1 {
    display: flex;
  }
  .profile__section-2 {
    flex: 1;
    padding-left: 10%;
    display: flex;
    flex-direction: column;
    gap: 10px;
    font-size: 14px;
  }
  .profile__section-3 {
    width: 100%;
    padding: 0 5px;
    display: flex;
    justify-content: space-between;
  }
  .profile__section-4 {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 5px;
  }
  .profile__section-5 {
    padding: 10px 4px;
    display: flex;
    flex-direction: column;
    gap: 4px;

    .nickname {
      font-weight: 500;
    }
    .introduce {
      font-size: 14px;
    }
  }
`;

export default Profile;
