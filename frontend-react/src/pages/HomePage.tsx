import { useEffect, useState } from 'react';
import axios from 'axios';
import LoadingPage from '../components/LoadingPage';
import {formatTime} from "../utils/formatTime.ts";

const HomePage = () => {
    const [isLoading, setIsLoading] = useState(true);

    const [todayProcessStats, setTodayProcessStats] = useState<[]>([]);
    const [todaySummedTime, setTodaySummedTime] = useState<number>(0);
    const [todayTopApplication, setTodayTopApplication] = useState<[]>([]);
    const [todayTopCategory, setTodayTopCategory] = useState<[]>([]);
    const [todayTimeStats, setTodayTimeStats] = useState<Record<number, number>>({});
    const [chartLabels, setChartLabels] = useState<string[]>([]);

    useEffect(() => {
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
                setTodaySummedTime(todayTime);

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
                setIsLoading(false);
            });
    }, []);


    if (isLoading) return <LoadingPage />;

    {console.log(todayProcessStats)}
    {console.log(todayTopApplication)}
    {console.log(todayTopCategory)}
    {console.log(todayTimeStats)}
    {console.log(chartLabels)}
    {console.log(todaySummedTime)}

    return (
        <div className="bg-[#131316] border-2 border-purple-500/30 rounded-2xl shadow-[0_0_50px_rgba(168,85,247,0.25)] text-center w-5/6">

            <h1 className={`text-purple-500 p-8 text-3xl border-2 border-purple-500/30 rounded-2xl m-5 font-black`}>Let's see how <span className={`text-white underline`}>you</span> spent <span className={`text-white underline`}>your</span> day!</h1>

            <div className="flex flex-row justify-center border-purple-500/30 rounded-2xl gap-10">

                <div className="w-2/7 h-30 border-2 border-purple-500/30 rounded-2xl text-purple-500 ">
                    Suma Czasu Dzisiaj
                </div>

                <div className="w-2/7 border-2 border-purple-500/30 rounded-2xl text-purple-500 ">
                    Top Aplikacja dnia
                </div>

                <div className="w-2/7 border-2 border-purple-500/30 rounded-2xl text-purple-500 ">
                    Kategoria Dnia
                </div>

            </div>


            <div className={`m-5 h-100 border-2 border-purple-500/30 rounded-2xl text-purple-500`}>
                Wykres dzisiejszy
            </div>

            <div className={`m-5 h-1/5 border-2 border-purple-500/30 rounded-2xl text-purple-500`}>
                Ostatnie aktywne procesy
            </div>

        </div>
        )
}
export default HomePage;