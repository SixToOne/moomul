import React from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

const Landing = () => {
  return (
    <StyledLanding>
      <div className="container">
        <div className="blobs">
          <div className="liquid"></div>
          <div className="blob"></div>
          <div className="blob"></div>
          <div className="blob"></div>
          <div className="blob"></div>
          <div className="blob"></div>
          <div className="blob"></div>
          <div className="blob"></div>
          <div className="blob"></div>
        </div>
        <h1 className="text">
          무물
          <SiteInfo>
            {/* <span>무엇이든 물어봐~</span> */}
            <Link to="/login">로그인 하러가기</Link>
          </SiteInfo>
        </h1>
      </div>
      <div className=""></div>
      <svg xmlns="http://www.w3.org/2000/svg" version="1.1" height="0">
        <defs>
          <filter id="goog">
            <feGaussianBlur in="SourceGraphic" stdDeviation="10" result="blur" />
            <feColorMatrix
              in="blur"
              mode="matrix"
              values="1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 18 -7"
              result="goo"
            />
            <feBlend in="SourceGraphic" in2="goo" />
          </filter>
          <filter id="goo">
            <feGaussianBlur in="SourceGraphic" stdDeviation="10" result="blur" />
            <feColorMatrix
              in="blur"
              mode="matrix"
              values="1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 18 -7"
              result="goo"
            />
            <feBlend in="SourceGraphic" in2="goo" />
          </filter>
        </defs>
      </svg>
    </StyledLanding>
  );
};

const StyledLanding = styled.div`
  b {
    font-size: 28px;
    color: #fff;
  }

  width: 100%;
  height: 100vh;

  .container {
    width: 100%;
    height: 100%;
    background: ${({ theme }) => theme.PRIMARY};
    overflow: hidden;
    position: relative;
  }

  .text {
    position: absolute;
    left: 50%;
    top: 45%;
    transform: translate(-50%, -50%);
    text-align: center;
    color: ${({ theme }) => theme.PRIMARY};
    font-size: 64px;
    font-weight: 900;
  }

  .liquid {
    width: 120%;
    height: 10px;
    position: absolute;
    top: -2%;
    left: -10%;
    background: #fff;
    border-radius: 10%;
    animation: liquid 5s infinite;
  }

  .blobs {
    filter: url('#goo');
    width: 100%;
    height: 100%;
    position: relative;
  }
  .blobs .blob {
    width: 30px;
    height: 30px;
    margin: 0 5px 0px 0;
    background: #fff;
    border-radius: 50%;
    position: absolute;
    top: 0;
    animation: drip_one 5s infinite;
  }
  .blobs .blob:nth-child(1) {
    left: -14%;
  }
  .blobs .blob:nth-child(2) {
    left: -1%;
  }
  .blobs .blob:nth-child(3) {
    left: 12%;
  }
  .blobs .blob:nth-child(4) {
    left: 25%;
  }
  .blobs .blob:nth-child(5) {
    left: 38%;
  }
  .blobs .blob:nth-child(6) {
    left: 51%;
  }
  .blobs .blob:nth-child(7) {
    left: 64%;
  }
  .blobs .blob:nth-child(8) {
    left: 77%;
  }
  .blobs .blob:nth-child(9) {
    left: 90%;
  }
  .blobs .blob:nth-of-type(4n-7) {
    width: 65px;
    animation: drip_four 5s infinite;
  }
  .blobs .blob:nth-of-type(3n-2) {
    width: 26px;
  }
  .blobs .blob:nth-of-type(2) {
    width: 22px;
  }
  .blobs .blob:nth-of-type(8) {
    animation: drip_five 5s infinite;
  }
  .blobs .blob:nth-of-type(4n + 2) {
    height: 56px;
    animation: drip_two 5s infinite;
  }
  .blobs .blob:nth-of-type(6n-2) {
    height: 42px;
    animation: drip_three 5s infinite;
  }

  @keyframes drip_one {
    from {
      top: 0;
    }
    to {
      top: 103%;
    }
  }
  @keyframes drip_two {
    from {
      top: 0;
    }
    to {
      top: 104%;
    }
  }
  @keyframes drip_three {
    from {
      top: 0;
      height: 52px;
    }
    to {
      top: 102%;
      height: 132px;
    }
  }
  @keyframes drip_four {
    from {
      top: 0;
      width: 65px;
      height: 30px;
    }
    to {
      top: 102%;
      width: 75px;
      height: 45px;
    }
  }
  @keyframes drip_five {
    from {
      top: 0;
      height: 30px;
    }
    to {
      top: 102%;
      height: 72px;
    }
  }
  @keyframes liquid {
    from {
      height: 15px;
    }
    to {
      height: 109%;
    }
  }
`;

const SiteInfo = styled.div`
  font-size: 24px;
  margin-top: 10px;

  a {
    font-size: 16px;
    margin-top: 10px;
    color: ${({ theme }) => theme.PRIMARY};
    text-decoration: underline;
  }
`;

export default Landing;
