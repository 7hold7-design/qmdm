import DevicesTable from '../components/DevicesTable'

export default function Devices() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold text-gray-900">Устройства</h1>
        <button className="btn-primary">
          Добавить устройство
        </button>
      </div>
      
      <DevicesTable />
    </div>
  )
}
