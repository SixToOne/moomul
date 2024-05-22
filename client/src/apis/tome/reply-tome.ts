import axios from 'axios';

const path = '/tome';

export const replyTome = async (username: string, tomeId: number, reply: string) => {
  const res = await axios.post(`${path}/${tomeId}/replies?username=${username}`, { reply });
  return res.data;
};
