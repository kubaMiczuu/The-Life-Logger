import SidebarItem from "./SidebarItem.tsx";
import {BarChart2, House, Settings, CircleQuestionMark, Wrench} from "lucide-react";

const Sidebar = () => {
    return (
        <div className="bg-[#131316] border-2 border-purple-500/30 p-8 rounded-2xl shadow-[0_0_50px_rgba(168,85,247,0.25)] text-center w-1/6">
            <h1 className="text-4xl font-black text-white mb-2 border-b-5 rounded-2xl border-purple-500 p-3 pt-0 cursor-default">
                LIFE<span className="text-purple-500">LOGGER</span>
            </h1>

            <div className="flex flex-col text-white pt-8 text-xl h-14/15">
                <div className="flex flex-col gap-2">
                    <SidebarItem name="Home Page" to="/" icon={<House size={20} />} />
                    <SidebarItem name="Statistics" to="/stats" icon={<BarChart2 size={20} />} />
                    <SidebarItem name="Rules" to="/rules" icon={<Wrench size={20} />} />
                </div>

                <div className={`flex-1`}></div>

                <div className="flex flex-col gap-2">
                    <SidebarItem name="Settings" to="/settings" icon={<Settings size={20}/>} />
                    <SidebarItem name="About" to="/about" icon={<CircleQuestionMark size={20} />} />
                </div>
            </div>
        </div>
    )
}
export default Sidebar