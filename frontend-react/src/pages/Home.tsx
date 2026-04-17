import { useEffect, useState } from 'react';
import axios from 'axios';

import Loading from './Loading.tsx';
import HomePageDailyChart from '../components/HomePageDailyChart.tsx'
import HomePageBasicStatistics from "../components/HomePageBasicStatistics.tsx";
import HomePageRecentActivity from "../components/HomePageRecentActivity.tsx";

import {formatTime} from "../utils/formatTime.ts";

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
        <div className="lg:w-4/5 bg-[#131316] border-2 border-purple-500/30 rounded-2xl shadow-[0_0_50px_rgba(168,85,247,0.25)] text-center">

            <h1 className={`lg:h-[10%] lg:m-[2%] defaultColorFormat p-5 text-4xl border-2 border-purple-500/30 rounded-2xl m-5 font-['JetBrains_Mono',monospace]`}>Let's see how did <span className={`whiteColorFormat`}>you</span> spent <span className={`whiteColorFormat`}>your</span> day!</h1>

            <HomePageBasicStatistics todaySummedTime={todaySummedTime} todayTopApplication={todayTopApplication} todayTopCategory={todayTopCategory}/>

            <HomePageDailyChart chartData={formattedChartData} />

            <HomePageRecentActivity todayProcessStats={todayProcessStats} />

        </div>
        )
}
export default Home;