import axios from 'axios';

const path = `/fromme`;

export const patchFromMe = async (username: string, tomeId: number, optionId: number) => {
  const res = await axios.patch(`${path}/${tomeId}/votes?username=${username}`, {
    voted: optionId,
  });
  return res.data;
};
