import { useEffect, useState } from 'react';
import axios from 'axios';
import Loading from './Loading.tsx';
import {formatTime} from "../utils/formatTime.ts";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, Cell, ResponsiveContainer} from "recharts";
import {data} from "react-router-dom";

const Home = () => {
    const [isLoading, setIsLoading] = useState(true);

    const [todayProcessStats, setTodayProcessStats] = useState<[]>([]);
    const [todaySummedTime, setTodaySummedTime] = useState<string>("");
    const [todayTopApplication, setTodayTopApplication] = useState<any>([]);
    const [todayTopCategory, setTodayTopCategory] = useState<any>([]);
    const [todayTimeStats, setTodayTimeStats] = useState<number[]>([]);
    const [chartLabels, setChartLabels] = useState<string[]>([]);

    const fetchData = async () => {
        axios.get('http://localhost:8080/api/stats/summary', {
            params: {range: "daily", startDate:"2026-03-19"}
        })
            .then(res => {
                const data = res.data;
                const processData = data.processStats;
                const categoryData = data.categoryStats;
                const timeData: Record<number, number> = data.timeStats;

                let todayTime = 0;
                processData.map((data: { durationSeconds: number; }) => {
                    todayTime += data.durationSeconds;
                })
                const hours = Math.floor(todayTime / 3600);
                const minutes = Math.floor((todayTime % 3600) / 60);
                const seconds = todayTime % 60;
                const normalizedTime = `${hours} ${hours===1?'hour':'hours'} ${minutes} ${minutes===1?'minute':'minutes'} and ${seconds} ${seconds===1?'second':'seconds'}`;
                setTodaySummedTime(normalizedTime);

                setTodayTopApplication(processData[0]);

                setTodayTopCategory(categoryData[0]);

                setTodayProcessStats(data.processStats);


                let timeStats: number[] = [];
                let labels: string[] = [];

                for(let i = 0; i < 24; i++) {
                    const seconds = timeData[i] || 0;
                    timeStats.push(seconds);
                    labels.push(formatTime(24, seconds));
                }

                setTodayTimeStats(timeStats);
                setChartLabels(labels);

                setIsLoading(false);
            })
            .catch(err => {
                console.log(err)
            });
    }

    useEffect(() => {
        fetchData().then();
    }, []);

    const formattedChartData = todayTimeStats.map((value, index) => ({
        hour: index,
        seconds: value,
        display: chartLabels[index]
    }))

    if (isLoading) return <Loading fetchData={fetchData} />;

    return (
        <div className="bg-[#131316] border-2 border-purple-500/30 rounded-2xl shadow-[0_0_50px_rgba(168,85,247,0.25)] text-center w-5/6">

            <h1 className={`text-purple-500 p-5 text-4xl border-2 border-purple-500/30 rounded-2xl m-5 font-['JetBrains_Mono',monospace] font-black`}>Let's see how did <span className={`text-white underline`}>you</span> spent <span className={`text-white underline`}>your</span> day!</h1>

            <div className="flex flex-row justify-center border-purple-500/30 rounded-2xl gap-10">

                <div className="w-3/10 h-30 border-2 border-purple-500/30 rounded-2xl flex flex-col justify-center items-center gap-1">
                    <span className={`text-3xl font-black text-purple-500`}><span className={`text-white underline`}>You</span> spent:</span>
                    <span className={`text-2xl font-['JetBrains_Mono',monospace] font-black text-purple-500 underline`}>{todaySummedTime}</span>
                    <span className={`text-xl font-['JetBrains_Mono',monospace] font-black text-white`}>before screen today... Woah...</span>
                </div>

                <div className="w-3/10 border-2 border-purple-500/30 rounded-2xl flex flex-col justify-center items-center gap-1">
                    <span className={`text-3xl font-black text-purple-500`}><span className={`text-white underline`}>You</span> spent the most time on:</span>
                    <span className={`text-2xl font-['JetBrains_Mono',monospace] font-black text-purple-500 underline`}>{todayTopApplication.processName}</span>
                    <span className={`text-xl font-['JetBrains_Mono',monospace] font-black text-white`}>this application really got you today!</span>
                </div>

                <div className="w-3/10 border-2 border-purple-500/30 rounded-2xl flex flex-col justify-center items-center gap-1">
                    <span className={`text-3xl font-black text-purple-500`}><span className={`text-white underline`}>Your</span> category of the day is:</span>
                    <span className={`text-2xl font-['JetBrains_Mono',monospace] font-black text-purple-500 underline`}>{todayTopCategory.category}</span>
                    <span className={`text-xl font-['JetBrains_Mono',monospace] font-black text-white`}>this category is really something...</span>
                </div>

            </div>


            <div className={`m-5 h-6/11 border-2 border-purple-500/30 rounded-2xl text-purple-500`}>

                <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={formattedChartData}>
                        <XAxis
                            dataKey="hour"
                            stroke="#a855f7"
                            tickFormatter={(value) => `${value}:00`}
                        />
                        <YAxis stroke="#a855f7" hide />

                        <Tooltip
                            cursor={{fill: 'rgba(168, 85, 247, 0.1)'}}
                            content={({ active, payload }) => {
                                if (active && payload && payload.length) {
                                    return (
                                        <div className="bg-[#131316] border border-purple-500 p-2 rounded shadow-lg">
                                            <p className="text-white font-mono">{`${payload[0].payload.hour}:00`}</p>
                                            <p className="text-purple-400 font-bold">{payload[0].payload.display}</p>
                                        </div>
                                    );
                                }
                                return null;
                            }}
                        />

                        <Bar dataKey="seconds">
                            {formattedChartData.map((entry, index) => (
                                <Cell
                                    key={`cell-${index}`}
                                    fill={entry.seconds > 0 ? "#ad46ff" : "transparent"}
                                />
                            ))}
                        </Bar>
                    </BarChart>
                </ResponsiveContainer>

            </div>

            <div className={`m-5 h-1/8 border-2 border-purple-500/30 rounded-2xl text-purple-500`}>

                <span className={`text-center text-purple-500 text-2xl font-black`}><span className={`text-white underline`}>Recent</span> active processes</span>

                <div className={`flex flex-row text-purple-500 text-lg flex-wrap justify-center items-center gap-x-12 gap-y-2 p-5`}>
                    {todayProcessStats.slice(0,5).map((process,index) =>
                        <span className={`font-['JetBrains_Mono',monospace] font-black whitespace-nowrap`}><span className={`text-white underline`}>{index+1}.</span> {process.processName} </span>
                    )}
                </div>

            </div>

        </div>
        )
}
export default Home;