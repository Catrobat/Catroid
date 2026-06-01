/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.ui.aiassist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import org.catrobat.aitutor.ui.`public`.AiTutorView
import org.catrobat.catroid.R
import org.catrobat.catroid.databinding.FragmentAiAssistBinding
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.utils.ToastUtil

class AiAssistFragment : Fragment() {

    private var _binding: FragmentAiAssistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiAssistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val structure = arguments?.getString("structure")
//        binding.textAiAssist.text = structure ?: ""

        binding.composeAiTutor.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                var show by remember { mutableStateOf(true) }
                AiTutorView(
                    show = show,
                    onDismissRequest = { show = false },
                    codeContext = structure,
                    onClipboardPaste = { pastedText ->
                        val sprite = XstreamSerializer.getInstance().getSpriteFromXmlString(pastedText)
                        if (sprite == null) {
                            ToastUtil.showError(requireContext(), R.string.ai_tutor_invalid_xml)
                        } else {
                            parentFragmentManager.setFragmentResult(
                                AI_TUTOR_RESULT_KEY,
                                Bundle().apply { putString(AI_TUTOR_XML_KEY, pastedText) }
                            )
                            show = false
                            parentFragmentManager.popBackStack()
                        }
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String = AiAssistFragment::class.java.simpleName
        const val AI_TUTOR_RESULT_KEY = "ai_tutor_result"
        const val AI_TUTOR_XML_KEY = "spriteXml"
    }
}
