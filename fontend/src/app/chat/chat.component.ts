
import { ChatRoom } from './../model/ChatRoom';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ModuleCommon } from '../moduleCommon.component';
import { WebSocketService } from '../service/websocket.service';
import { Subscription } from 'rxjs';
import { ChatRoomService } from '../service/chatRoom.service';
import { MessageService } from '../service/message.service';
import { FileService } from '../service/file.service';
import { Router } from '@angular/router';
import { Member } from '../model/Member';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [ModuleCommon],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css'],
})
export class ChatComponent implements OnInit, OnDestroy {
  messages: any = [];
  newMessage: string = '';
  messageSubscription!: Subscription;
  user!: any;
  chatRooms: any = [];
  curentRoomId!: any;
  roomName: string = '';
  showPopup: boolean = false;
  roomNameKey: string = '';
  pageSize: number = 20;
  currentPage: number = 1;
  totalPages: number = 0;
  upLoadFlag : boolean = false;
  userCount : number = 0
  constructor(
    private webSocketService: WebSocketService,
    private chatRoomService: ChatRoomService,
    private messageService: MessageService,
    private fileService : FileService,
    private router : Router
  ) {}

  ngOnInit(): void {
    this.user = this.getItemWithExpiry('user');
    setInterval(() => {
      this.getItemWithExpiry('user');
    }, 60 * 1000);
    this.webSocketService.connect();

    this.messageSubscription = this.webSocketService
      .getMessage()
      .subscribe((message) => {
        if (message) {
          console.log(message)
          let mes = JSON.parse(message);
          switch(mes.type){
            case "CHAT": 
            this.messages.push(mes);
            break;
            case "COUNT":
              this.userCount = mes.countMember;
              break;
          }

        }
      });
    this.getAllRoom();
  }

  ngOnDestroy(): void {
    if (this.messageSubscription) {
      this.messageSubscription.unsubscribe();
    }
    this.webSocketService.disconnect();
  }
  getMessInRoom(roomId: string) {
    this.messageService.getMessInRoom(roomId).subscribe((data) => {
      if (data) {
        this.messages = data;
      }
    });
  }
  getAllRoom() {
    this.chatRoomService.getLstRoom().subscribe((data) => {
      this.chatRooms = data;
      this.totalPages = Math.ceil(this.chatRooms.length / this.pageSize)
    });
  }
  sendMessage() {
    this.webSocketService.sendMessage(
      this.user.id,
      this.user.username,
      this.newMessage,
      this.curentRoomId,
      '',
      ''
    );
    this.newMessage = '';
  }
  isSentByCurrentUser(index: number) {
    if (this.messages && this.messages[index].sender == this.user.id) {
      return true;
    } else {
      return false;
    }
  }
  chooseRoom(id: string) {
   
    let member : Member = new Member();
    member.roomId = id;
    member.status = "JOIN"
    member.userId = this.user.id
    this.chatRoomService.changeMember(member).subscribe((data : any)=> {
      if(data < 0){
          alert("Chat room is full")
      }else {
        this.curentRoomId = id;
        this.getMessInRoom(id);
        this.userCount = data;
        this.webSocketService.sendCountMember(this.curentRoomId,this.userCount)
      }
    })
   
  }
  openDialog() {
    this.showPopup = true; // Mở popup
  }

  closeDialog() {
    this.showPopup = false; // Đóng popup
  }

  registerRoom() {
    if (this.roomName.trim()) {
      // Xử lý logic đăng ký phòng ở đây
      console.log('Đăng ký phòng:', this.roomName);
      let chatRoom: ChatRoom = new ChatRoom();
      chatRoom.roomId = '';
      chatRoom.roomName = this.roomName;
      this.chatRoomService.createRoom(chatRoom).subscribe((data) => {
        if (data) {
          let room = data as ChatRoom;
          this.curentRoomId = room.roomId;
          let member : Member = new Member();
          member.roomId = this.curentRoomId;
          member.status = "JOIN"
          member.userId = this.user.id
          this.chatRoomService.changeMember(member).subscribe((data : any)=> {
            
          })
          this.getAllRoom();
        }
      });
      // Sau khi đăng ký, đóng popup và reset trường
      this.closeDialog();
      this.roomName = '';
    }
  }
  findRomByName() {
    this.chatRoomService
      .getLstRoomByName(this.roomNameKey)
      .subscribe((data) => {
        if (data) {
          this.chatRooms = data;
          this.totalPages = Math.ceil(this.chatRooms.length / this.pageSize)
        }
      });
  }
  // LẤY CÁC PHÒNG ĐANG HIỂN THỊ TRONG TRANG HIỆN TẠI
  get paginatedRooms() {
    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;
    return this.chatRooms.slice(start, end);
  }

  // CHUYỂN SANG TRANG TRƯỚC
  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  // CHUYỂN SANG TRANG KẾ TIẾP
  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }
  uploadFile(event : any){
    const file = event.target.files[0];
    if (file) {
      const formData = new FormData();
      formData.append('file', file);
      let fileName = file.name
       console.log(file)
       this.fileService.uploadFile(formData).subscribe((data : any)=> {
       if (data) {
          
          console.log('FILE ĐÃ UPLOAD THÀNH CÔNG', data);
          //GỬI THÔNG BÁO QUA WEBSOCKET KHI UPLOAD THÀNH CÔNG
          this.webSocketService.sendMessage(
            this.user.id,
            this.user.username,
            this.newMessage,
            this.curentRoomId,
            data.fileName,
            data.fileUrl
          );
          
        }
       })
    }
  }
  triggerFileUpload() {
    document.getElementById('file-input')?.click();
  }
  checkShow(index : number){
    let showFlag = false
    if(index == 0){
      showFlag = true
    }else {
      if(this.messages[index].sender != this.messages[index -1].sender) {
        showFlag = true
      }
      if(this.messages[index].sender == this.messages[index -1].sender){
        const timestamp1 = this.messages[index].timestamp;
        const timestamp2 = this.messages[index - 1].timestamp;
        const timeDifference = Math.abs(timestamp1 - timestamp2);

        // CHUYỂN ĐỔI MILLISECOND SANG PHÚT
        const timeDifferenceInMinutes = timeDifference / (1000 * 60);
        if (timeDifferenceInMinutes <= 5) {
          showFlag = false;
        } else {
          showFlag = true;
        }
      }
    }
    return showFlag;
  }
  dowloadFile(id: string,fileName : string){
    this.fileService.dowloadFile(id).subscribe((data : any)=>{
      const a = document.createElement('a');
      const objectUrl = URL.createObjectURL(data);
      a.href = objectUrl;
      a.download = fileName;  // TÊN FILE MUỐN LƯU
      a.click();
      URL.revokeObjectURL(objectUrl);
    })
  }
  getItemWithExpiry(key: string) {
    const itemStr = localStorage.getItem(key);
  
    // NẾU KHÔNG CÓ DỮ LIỆU, TRẢ VỀ `NULL`
    if (!itemStr) {
      return null;
    }
  
    const item = JSON.parse(itemStr);
    const now = new Date();
    // KIỂM TRA NẾU ĐÃ HẾT HẠN
    if (now.getTime() > item.expiry) {
      // XÓA DỮ LIỆU NẾU ĐÃ HẾT HẠN
      localStorage.removeItem(key);
      this.router.navigate(['login'])
      return null;
    }
  
    return item.value;
  }
  leaveRoom(){
    let member : Member = new Member();
    member.roomId = this.curentRoomId;
    member.status = "MOVE"
    member.userId = this.user.id
    this.chatRoomService.changeMember(member).subscribe((data : any)=> {
      this.webSocketService.sendCountMember(this.curentRoomId,data)
        this.curentRoomId = '';
    })
  }
}
