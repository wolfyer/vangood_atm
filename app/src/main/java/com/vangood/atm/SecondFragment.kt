package com.vangood.atm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.vangood.atm.databinding.FragmentSecondBinding
import com.vangood.atm.databinding.RowChatroomBinding
import okhttp3.*
import okio.ByteString
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    val rooms = mutableListOf<Lightyear>()
    private lateinit var  adapter :ChatRoomAdapter
    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var websocket: WebSocket
    val TAG = SecondFragment::class.java.simpleName
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        //Web socket
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("wss://lott-dev.lottcube.asia/ws/chat/chat:app_test?nickname=Wolfyer")
            .build()
        websocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Log.d(TAG, ": onClosed");
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.d(TAG, ": onClosing");
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.d(TAG, ": onFailure");
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.d(TAG, ": onMessage $text");
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                Log.d(TAG, ": onMessage ${bytes.hex()}");
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.d(TAG, ": onOpen");
//                webSocket.send("Hello, I am Hank")
            }
        })
        binding.bSend.setOnClickListener {
            val message = binding.edMessage.text.toString()
//            val json = "{\"action\": \"N\", \"content\": \"$message\" }"
//            websocket.send(json)
//            val j = Gson().toJson(Message("N",message))
            websocket.send(Gson().toJson(MessageSend("N",message)))
        }
        binding.recycler.setHasFixedSize(true)
        //binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.layoutManager = GridLayoutManager(requireContext(),2)
        adapter = ChatRoomAdapter()
        binding.recycler.adapter = adapter


        //test message
        thread {
            val json =URL("https://api.jsonserve.com/qTrtTg").readText()
            val msg = Gson().fromJson(json,Message::class.java)
            Log.d(TAG, "msg: ${msg.body.text}")
        }
        //test chatroom list
        thread {
            val json =URL("https://api.jsonserve.com/qHsaqy").readText()
            val msgroom = Gson().fromJson(json,ChatRooms::class.java)
            Log.d(TAG, "msg rooms num: ${msgroom.result.lightyear_list.size} ")
            Log.d(TAG, "msg room: ${msgroom.result.lightyear_list[0].background_image}")
            //fill list with new coming data
            rooms.clear()
            rooms.addAll(msgroom.result.lightyear_list)
            //List<LightYear>
            activity?.runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }
    }
    inner class ChatRoomAdapter :RecyclerView.Adapter<ChatRoomViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
            val binding = RowChatroomBinding.inflate(layoutInflater,parent,false)
            return ChatRoomViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
            val lightYear= rooms[position]
            holder.host.setText(lightYear.stream_title)
            holder.title.setText(lightYear.nickname)
            Glide.with(this@SecondFragment).load(lightYear.head_photo)
                .into(holder.head)

        }

        override fun getItemCount(): Int {
            return rooms.size
        }

    }
    //use binding
    inner class ChatRoomViewHolder(val binding :RowChatroomBinding): RecyclerView.ViewHolder(binding.root){
        val host = binding.tvA
        val title = binding.tvB
        val head = binding.headShot
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
