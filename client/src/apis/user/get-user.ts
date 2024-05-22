import axios from 'axios';

const path = '/users/profile';

export const getUserProfile = async (username: string) => {
  const res = await axios.get(`${path}?username=${username}`);
  return res.data;
};
