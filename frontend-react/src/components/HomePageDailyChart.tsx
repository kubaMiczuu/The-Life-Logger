import { BarChart, Bar, XAxis, Tooltip,  Cell, ResponsiveContainer} from "recharts";

interface chartDataPoint {
    hour: number;
    seconds: number;
    display: string;
}

interface dailyChartProp {
    chartData: chartDataPoint[];
}

const HomePageDailyChart = ({chartData}:dailyChartProp) => {
    return (
        <div className={`lg:m-[2%] lg:h-[45%] border-2 border-purple-500/30 rounded-2xl`}>

            <span className={`text-2xl defaultColorFormat font-['JetBrains_Mono',monospace]`}><span className={`whiteColorFormat`}>Daily</span> Activity Distribution</span>

            <ResponsiveContainer width="100%" height="100%">
                <BarChart data={chartData} margin={{bottom: 30, top: 20 }}>
                    <XAxis
                        dataKey="hour"
                        stroke="#a855f7"
                        tickFormatter={(value) => `${value}:00`}
                    />

                    <Tooltip
                        cursor={{fill: 'rgba(168, 85, 247, 0.1)'}}
                        content={({ active, payload }) => {
                            if (active && payload && payload.length) {
                                return (
                                    <div className="bg-[#131316] border border-purple-500 p-2 rounded shadow-lg">
                                        <p className="text-white font-mono">{`${payload[0].payload.hour}:00-${payload[0].payload.hour}:59`}</p>
                                        <p className="text-purple-400 font-bold">{payload[0].payload.display}</p>
                                    </div>
                                );
                            }
                            return null;
                        }}
                    />

                    <Bar dataKey="seconds">
                        {chartData.map((entry, index) => (
                            <Cell
                                key={`cell-${index}`}
                                fill={entry.seconds > 0 ? "#ad46ff" : "transparent"}
                            />
                        ))}
                    </Bar>
                </BarChart>
            </ResponsiveContainer>

        </div>
    )
}

export default HomePageDailyChart;