import { useEffect, useState } from 'react';
import axios from 'axios';
import Loading from './Loading.tsx';
import {formatTime} from "../utils/formatTime.ts";

const Home = () => {
    const [isLoading, setIsLoading] = useState(true);

    const [todayProcessStats, setTodayProcessStats] = useState<[]>([]);
    const [todaySummedTime, setTodaySummedTime] = useState<string>("");
    const [todayTopApplication, setTodayTopApplication] = useState<any>([]);
    const [todayTopCategory, setTodayTopCategory] = useState<any>([]);
    const [todayTimeStats, setTodayTimeStats] = useState<Record<number, number>>({});
    const [chartLabels, setChartLabels] = useState<string[]>([]);

    const fetchData = async () => {
        axios.get('http://localhost:8080/api/stats/summary', {
            params: {range: "daily"}
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

                setTodayTimeStats(timeData);

                let labels: string[] = [];
                for(let i= 0; i < 24; i++) {
                    const seconds = timeData[i] || 0;
                    labels.push(formatTime(24, seconds));
                }
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


            <div className={`m-5 h-6/13 border-2 border-purple-500/30 rounded-2xl text-purple-500`}>
                Wykres dzisiejszy
            </div>

            <div className={`m-5 h-1/5 border-2 border-purple-500/30 rounded-2xl text-purple-500`}>
                Ostatnie aktywne procesy
            </div>

        </div>
        )
}
export default Home;