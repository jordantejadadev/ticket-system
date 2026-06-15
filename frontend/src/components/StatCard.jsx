const StatCard = ({ title, value }) => {
  return (
    <div className="bg-white rounded-xl p-6 shadow-sm">
      <h3 className="text-zinc-500 text-sm mb-2">{title}</h3>
      <p className="text-3xl font-bold">{value}</p>
    </div>
  )
}

export default StatCard;