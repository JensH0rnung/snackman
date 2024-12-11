import type {ISquareDTD} from "@/stores/Square/ISquareDTD";

type EventType = 'SNACK' | 'CALORIES'
type ChangeType = 'CREATE' | 'UPDATE' | 'DELETE'

export interface IFrontendMessageEvent {
  eventType: EventType,
  changeType: ChangeType,
  square: ISquareDTD
}

export interface IFrontendCaloriesMessageEvent {
  eventType: EventType,
  changeType: ChangeType,
  calories: number,
  message?: string
}
