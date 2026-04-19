import {useEffect, useState} from "react";

const Loading = ({fetchData}:any) => {

    const [timer, setTimer] = useState(0);

    useEffect(() => {
        const timer = setInterval(() => {
            setTimer(prevState => prevState + 1);
        }, 1000)

        return () => clearInterval(timer);
    }, []);

    return (
        <div className="w-4/5 flex flex-col items-center justify-center p-4">

            <div className="bg-[#131316] border border-purple-500/30 lg:p-15 rounded-2xl shadow-[0_0_50px_rgba(168,85,247,0.25)] text-center flex flex-col justify-center items-center">

                <h1 className="lg:text-6xl font-black text-white mb-2 border-b-4 rounded-2xl border-purple-500 p-3 pt-0 cursor-default">
                    LIFE<span className="defaultColorFormat">LOGGER</span>
                </h1>

                <p className="text-zinc-400 lg:text-2xl mt-5">Welcome to your life</p>

                <div className="mt-6 flex gap-3 justify-center">
                    <div className="h-2 w-2 rounded-full bg-purple-500 animate-ping"></div>
                    <span className="lg:text-xs uppercase tracking-widest text-purple-400 font-bold">Loading your data...</span>
                </div>

                <div className={`lg:w-1/4 grid transition-all duration-300 ease-in-out ${timer>5?"opacity-100 grid-rows-[1fr] mt-6":"opacity-0 grid-rows-[0fr] mt-0"}`}>
                    <button onClick={() => {fetchData(); setTimer(0)}} className={`${timer>5?"cursor-pointer":"cursor-default"} lg:text-xl uppercase text-purple-400 font-bold hover:scale-105 hover:underline-offset-8 hover:duration-200 ease-in-out transition hover:text-purple-500`}>Retry</button>
                </div>

            </div>

        </div>
    )
}

export default Loading;